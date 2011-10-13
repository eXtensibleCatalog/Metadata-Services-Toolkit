package xc.mst.repo;

import org.testng.annotations.Test;

import xc.mst.common.test.BaseTest;

public class RepositoryServiceTest extends BaseTest {

    @Test
    public void testSave() {
        if (null == getRepositoryService().save("^^^",
                "http://128.151.244.135:8080/OAIToolkit/oai-request.do?belgua",
                null,
                0)) {
            assert false;
        }
        if (null == getRepositoryService().save("#fun",
                "http://128.151.244.135:8080/OAIToolkit/oai-request.do?belgua",
                null,
                0)) {
            assert false;
        }
        if (null != getRepositoryService().save("hellofun",
                "http://128.151.244.135:8080/OAIToolkit/oai-request.do?belgua",
                null,
                0)) {
            assert false;
        }
    }

}
