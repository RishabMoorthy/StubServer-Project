package com.stubio.test;

import com.stubio.util.VirtualServiceMapper;
import com.stubio.util.VirtualServiceObject;

public class StubEngineUtilTest {

    public static void main(String[] args) {
        VirtualServiceMapper vsm = new VirtualServiceMapper("D:\\Stubio\\Projects\\RESTProject\\Virtual Service\\StubioVS_REST.vs");
        VirtualServiceObject vso = vsm.getVso();
        System.out.println(vso.getVsName());
    }
}
