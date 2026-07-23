package org.common.db.repository;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.hibernate.query.NativeQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Plain-Hibernate replacement for the Spring NamedParameterJdbcTemplate-based
 * metrics reporting queries. Uses native SQL (Oracle dialect) and returns
 * column-name keyed maps, matching the previous Java-Backend contract.
 *
 * Note: these reporting queries reference physical table names directly (as the
 * original JDBC queries did); they are not routed through the JPA naming strategy.
 */
public class MetricsRepository extends AbstractRepository {

    public List<Map<String, Object>> findLifetimeHits() {
        String sql = """
            SELECT vsname, SUM(total_count) AS TOTAL_COUNT,
              SUM(total_qacount) AS TOTAL_QA_COUNT, SUM(total_uatcount) AS TOTAL_UAT_COUNT
            FROM (
              SELECT vsname, SUM(count) AS total_count,
                SUM(qacount) AS total_qacount, SUM(perfcount) AS total_uatcount
              FROM READYAPI_MONTHLY_METRICS GROUP BY vsname
              UNION ALL
              SELECT vsname, SUM(count) AS total_count,
                SUM(CASE WHEN envtype='QA' THEN count ELSE 0 END) AS total_qacount,
                SUM(CASE WHEN envtype='PERF' THEN count ELSE 0 END) AS total_uatcount
              FROM READYAPI_DAILY_METRICS
              WHERE TO_CHAR(transdate,'MON-YYYY')=TO_CHAR(SYSDATE,'MON-YYYY')
              GROUP BY vsname
            ) combined GROUP BY vsname ORDER BY vsname
            """;
        return query(sql, Map.of());
    }

    public List<Map<String, Object>> findMonthlyHits(String fromMonth, String toMonth) {
        String sql = """
            SELECT vsname AS "serviceName", LOWER(month) AS "month", LOWER(year) AS "year",
              SUM(count) AS "totalCount", SUM(qacount) AS "totalQACount", SUM(perfcount) AS "totalUATCount"
            FROM READYAPI_MONTHLY_METRICS
            WHERE TO_DATE(month||'-'||year,'MON-YYYY')
              BETWEEN TO_DATE(:fromMonth,'MON-YYYY') AND TO_DATE(:toMonth,'MON-YYYY')
            GROUP BY vsname, month, year
            ORDER BY vsname, TO_DATE(month||'-'||year,'MON-YYYY')
            """;
        return query(sql, Map.of("fromMonth", fromMonth, "toMonth", toMonth));
    }

    public List<Map<String, Object>> findCustomReport(String fromDate, String toDate) {
        String sql = """
            SELECT VSNAME AS SERVICENAME, TO_CHAR(TRANSDATE,'DD-MON-YY') AS TRANSDATE,
              SUM(CASE WHEN ENVTYPE='QA' THEN COUNT ELSE 0 END) AS TOTALQACOUNT,
              SUM(CASE WHEN ENVTYPE='PERF' THEN COUNT ELSE 0 END) AS TOTALUATCOUNT,
              SUM(COUNT) AS TOTALCOUNT
            FROM READYAPI_DAILY_METRICS
            WHERE TRANSDATE BETWEEN TO_DATE(:fromDate,'DD/MM/YYYY') AND TO_DATE(:toDate,'DD/MM/YYYY')
            GROUP BY VSNAME, TRANSDATE ORDER BY TRANSDATE
            """;
        return query(sql, Map.of("fromDate", fromDate, "toDate", toDate));
    }

    public List<Map<String, Object>> findDormantServices(String serverIP) {
        String sql = """
            WITH active_vs AS (
              SELECT VSNAME FROM READYAPI_VS_CATALOG
              WHERE STATUS='Active' AND INSTR(VIRTSERVER,:serverIP)>0
            ), daily_data AS (
              SELECT VSNAME, SUM(COUNT) AS total_hits FROM READYAPI_DAILY_METRICS
              WHERE VIRTSERVERNAME=:serverIP AND TRANSDATE>=TRUNC(SYSDATE,'MM')
              AND VSNAME IN (SELECT VSNAME FROM active_vs) GROUP BY VSNAME
            ), monthly_data AS (
              SELECT VSNAME, MONTH, YEAR, SUM(COUNT) AS total_hits
              FROM READYAPI_MONTHLY_METRICS WHERE VIRTSERVER=:serverIP
              AND VSNAME IN (SELECT VSNAME FROM active_vs)
              AND ADD_MONTHS(TRUNC(SYSDATE,'MM'),-6)<=TO_DATE(YEAR||'-'||MONTH||'-01','YYYY-MM-DD')
              GROUP BY VSNAME, MONTH, YEAR
            ), last_3_months AS (
              SELECT VSNAME, SUM(NVL(total_hits,0)) AS hits_3m FROM (
                SELECT * FROM daily_data UNION ALL
                SELECT VSNAME, total_hits FROM monthly_data
                WHERE TO_DATE(YEAR||'-'||MONTH||'-01','YYYY-MM-DD')>=ADD_MONTHS(TRUNC(SYSDATE,'MM'),-3)
              ) GROUP BY VSNAME
            ), last_6_months AS (
              SELECT VSNAME, SUM(NVL(total_hits,0)) AS hits_6m FROM (
                SELECT * FROM daily_data UNION ALL SELECT VSNAME, total_hits FROM monthly_data
              ) GROUP BY VSNAME
            )
            SELECT a.VSNAME, NVL(l3.hits_3m,0) AS HITS_3M, NVL(l6.hits_6m,0) AS HITS_6M,
              CASE WHEN NVL(l3.hits_3m,0)=0 THEN 'count_0'
                   WHEN NVL(l3.hits_3m,0) BETWEEN 1 AND 50 THEN 'count_1_50'
                   WHEN NVL(l3.hits_3m,0) BETWEEN 51 AND 100 THEN 'count_51_100'
                   ELSE NULL END AS COUNT_CATEGORY_3M,
              CASE WHEN NVL(l6.hits_6m,0)=0 THEN 'count_0'
                   WHEN NVL(l6.hits_6m,0) BETWEEN 1 AND 50 THEN 'count_1_50'
                   WHEN NVL(l6.hits_6m,0) BETWEEN 51 AND 100 THEN 'count_51_100'
                   ELSE NULL END AS COUNT_CATEGORY_6M
            FROM active_vs a
            LEFT JOIN last_3_months l3 ON a.VSNAME=l3.VSNAME
            LEFT JOIN last_6_months l6 ON a.VSNAME=l6.VSNAME
            WHERE NVL(l3.hits_3m,0)<=100
            """;
        return query(sql, Map.of("serverIP", serverIP));
    }

    public List<Map<String, Object>> findResponseTime(String serviceName, String serverIP,
                                                      String fromUtc, String toUtc) {
        String sql = """
            SELECT dm.VSNAME, dm.VIRTSERVERNAME, rt.RESPID, rt.METRICSID,
              rt.STARTTIME, rt.ENDTIME, rt.AVGRESPTIME, rt.MAXRESPTIME, rt.AVGTPS
            FROM READYAPI_RESPONSE_TIME rt
            JOIN READYAPI_DAILY_METRICS dm ON rt.METRICSID=dm.METRICSID
            WHERE dm.VSNAME=:serviceName AND dm.VIRTSERVERNAME=:serverIP
            AND rt.STARTTIME<=TO_TIMESTAMP(:toDateTimeUtc,'YYYY-MM-DD"T"HH24:MI')
            AND rt.ENDTIME>=TO_TIMESTAMP(:fromDateTimeUtc,'YYYY-MM-DD"T"HH24:MI')
            ORDER BY rt.STARTTIME ASC
            """;
        return query(sql, Map.of(
                "serviceName", serviceName,
                "serverIP", serverIP,
                "fromDateTimeUtc", fromUtc,
                "toDateTimeUtc", toUtc));
    }

    /** Runs a native query and returns each row as a column-name keyed map. */
    private List<Map<String, Object>> query(String sql, Map<String, Object> params) {
        return executeReadOnly(session -> {
            NativeQuery<Tuple> q = session.createNativeQuery(sql, Tuple.class);
            params.forEach(q::setParameter);
            List<Tuple> tuples = q.getResultList();
            return tuples.stream().map(MetricsRepository::toMap).toList();
        });
    }

    private static Map<String, Object> toMap(Tuple tuple) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (TupleElement<?> el : tuple.getElements()) {
            String alias = el.getAlias();
            row.put(alias, tuple.get(alias));
        }
        return row;
    }
}
