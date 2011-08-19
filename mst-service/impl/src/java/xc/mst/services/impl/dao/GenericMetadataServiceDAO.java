package xc.mst.services.impl.dao;

import xc.mst.dao.MetadataServiceDAO;
import xc.mst.repo.Repository;
import xc.mst.services.MetadataService;

public class GenericMetadataServiceDAO extends MetadataServiceDAO {

    protected String getServiceName() {
        return ((MetadataService) config.getBean("MetadataService")).getServiceName();
    }

    protected Repository getRepository() {
        return ((MetadataService) config.getBean("MetadataService")).getRepository();
    }
}
