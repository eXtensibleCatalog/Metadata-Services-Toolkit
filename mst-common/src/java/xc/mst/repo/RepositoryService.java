/**
  * Copyright (c) 2010 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.repo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.harvester.ValidateRepository;
import xc.mst.manager.BaseService;
import xc.mst.utils.MSTConfiguration;

public class RepositoryService extends BaseService {

    private final static Logger LOG = Logger.getLogger(RepositoryService.class);
    public final static String FORMAT_INTEGRALS = "%,d";

    public List<Repository> getAll() {
        try {
            List<Repository> repos = getRepositoryDAO().getAll();
            List<Repository> repos4real = new ArrayList<Repository>();
            for (int i=0; i<repos.size(); i++) {
                Repository r = repos.get(i);
                if (r.getService() != null) {
                    r = getServicesService().getServiceById(r.getService().getId()).getMetadataService().getRepository();
                    if (r != null)
                        repos4real.add(r);
                } else if (r.getProvider() != null) {
                    r = getRepository(r.getProvider());
                    if (r != null)
                        repos4real.add(r);
                }
            }
            LOG.debug("repos4real: "+repos4real);
            return repos4real;
        } catch (Throwable t) {
            util.throwIt(t);
            return null;
        }
    }

    public Date getLastModified() {
        Date d = null;
        for (Repository r : getAll()) {
            if (getRepositoryDAO().ready4harvest(r.getName())) {
                Date d2 = getRepositoryDAO().getLastModified(r.getName());
                if (d2 != null && (d == null || d2.after(d))) {
                    d = d2;
                }
            }
        }
        return d;
    }

    public Repository getRepository(Provider p) {
        Repository repo = (Repository)config.getBean("Repository");
        repo.setProvider(p);
        repo.setName(p.getName());
        return repo;
    }

    public Record getRecord(long id) {
        try {
            for (Repository r : getAll()) {
                Record rec = r.getRecord(id);

                if (rec != null) {
                    injectSuccessors(rec);
                    injectPredecessors(rec);
                    getMessageService().injectMessages(rec);
                    if (r.getService() != null) {
                        rec.setService(r.getService());
                    } else if (r.getProvider() != null) {
                        rec.setProvider(r.getProvider());
                        r.injectHarvestInfo(rec);
                    } else {
                        LOG.error("neither service or provider set on r.getName(): "+r.getName());
                    }
                    return rec;
                }
            }
        } catch (Throwable t) {
            util.throwIt(t);
        }
        return null;
    }

    public void injectSuccessors(Record rec) {
        for (Repository r : getAll()) {
            r.injectSuccessors(rec);
        }
    }

    public void injectPredecessors(Record rec) {
        List<Long> preds = new ArrayList<Long>();
        for (Repository r : getAll()) {
            LOG.debug("checking "+r.getName()+ " for preds of "+rec.getId());
            List<Long> tempPreds = r.getPredecessorIds(rec);
            if (tempPreds != null) {
                preds.addAll(tempPreds);
            }
        }
        LOG.debug("preds: "+preds);
        for (Repository r : getAll()) {
            for (Long id : preds) {
                Record pred = r.getRecord(id);
                if (pred != null) {
                    LOG.debug("pred: "+pred);
                    rec.getPredecessors().add(pred);
                }
            }
        }
    }

    public String save(String repositoryName, String repositoryURL, Provider provider, long numberOfRecordsToHarvest) {
        try {
            boolean update = false;
            if (provider == null) {
                provider = new Provider();
            } else if (provider.getId() != -1) {
                update = true;
                provider = getProviderService().getProviderById(provider.getId());
                if(provider == null) {
                    getUserService().sendEmailErrorReport();
                    return "Error occurred while editing repository. An email has been sent to the administrator.";
                }
            }

            provider.setNumberOfRecordsToHarvest(numberOfRecordsToHarvest);
            provider.setName(repositoryName);
            provider.setOaiProviderUrl(repositoryURL);

            boolean urlChanged = false;
            if(update && provider.getOaiProviderUrl().equalsIgnoreCase(repositoryURL)) {
                urlChanged = false;
            } else {
                urlChanged = true;
            }

            Provider repositorySameName = getProviderService().getProviderByName(repositoryName);
            Provider repositorySameURL = getProviderService().getProviderByURL(repositoryURL);

            if (repositorySameName != null && repositorySameName.getId() != provider.getId()) {
                return "Repository with Name '"+repositoryName+"' already exists";
            }
            if (repositorySameURL != null && repositorySameURL.getId()!=provider.getId()) {
                return "Repository with URL '"+repositoryURL+"' already exists";
            }

            Pattern p = Pattern.compile("[^A-Za-z0-9_ ]");
            Matcher m = p.matcher(repositoryName);
            if (m.find()) {
                LOG.debug("bad pattern found");
                return "Repository name may only contain letters, numbers, underscores, and spaces";
            } else {
                LOG.debug("no bad pattern found");
            }
            if (repositoryName.length() > 25) {
                return "Repository name must be 25 characters or less";
            }

            provider.setUpdatedAt( new Timestamp(new Date().getTime()));
            if (!update) {
                provider.setCreatedAt(new Date());
                getProviderService().insertProvider(provider);
            } else {
                if (urlChanged) { //perform revalidation because repository URL has been changed
                    provider.setIdentify(false);
                    provider.setListFormats(false);
                    provider.setListSets(false);
                    provider.removeAllFormats();
                    provider.removeAllSets();
                }
                getProviderService().updateProvider(provider);
            }
            if (!update || urlChanged) {
                provider.setLastValidationDate(new Date());
                ValidateRepository vr = (ValidateRepository)MSTConfiguration.getInstance().getBean("ValidateRepository");
                try {
                    vr.validate(provider.getId());
                } catch (Throwable t) {
                    return t.getMessage();
                }
            }
            return null;
        } catch(DataException e) {
            LOG.error(e.getMessage(),e);
            return "Unable to access the database to get Repository information. There may be problem with database configuration.";
        }
    }

    public List<String[]> getIncomingHarvestRecords() {
        List<String[]> table = new ArrayList<String[]>();
        table.add(new String[] {"harvest_name-set", "active records", "updates", "deleted records"});
        List<Repository> repos = getAll();
        for (Repository repo : repos) {
            if (repo.ready4harvest() && repo.getProvider() != null) {
                List<String[]> dbRows = getRepositoryDAO().getAllPersistentProperties(repo.getName());
                Map<String, String[]> displayRows = new TreeMap<String, String[]>();
                if (dbRows != null) {
                    for (String[] dbRow : dbRows) {
                        if (!dbRow[0].startsWith("incoming")) {
                            continue;
                        }
                        int idx0 = dbRow[0].indexOf("-");
                        String key = repo.getName();
                        if (idx0 > -1) {
                            key = dbRow[0].substring(idx0+1);
                        }
                        String[] displayRow = displayRows.get(key);
                        if (displayRow == null) {
                            displayRow = new String[4];
                            displayRow[0] = key;
                            displayRows.put(key, displayRow);
                        }
                        if (dbRow[0].startsWith("incomingNewRecordsCount")) {
                            displayRow[1] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("incomingUpdatedRecordsCount")) {
                            displayRow[2] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("incomingDeletedRecordsCount")) {
                            displayRow[3] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        }
                    }
                }
                for (String[] displayRow : displayRows.values()) {
                    table.add(displayRow);
                }
            }
        }
        return table;
    }

    public List<String[]> getIncomingServiceRecords() {
        List<String[]> table = new ArrayList<String[]>();
        table.add(new String[] {"service_name-type", "active records", "updates", "deleted records", "unknown errors"});
        List<Repository> repos = getAll();
        for (Repository repo : repos) {
            if (repo.ready4harvest() && repo.getService() != null) {
                List<String[]> dbRows = getRepositoryDAO().getAllPersistentProperties(repo.getName());
                Map<String, String[]> displayRows = new TreeMap<String, String[]>();
                if (dbRows != null) {
                    for (String[] dbRow : dbRows) {
                        if (!dbRow[0].startsWith("incoming")) {
                            continue;
                        }
                        int idx0 = dbRow[0].indexOf("-");
                        String key = "";
                        if (idx0 > -1) {
                            key = dbRow[0].substring(idx0+1);
                        }
                        String[] displayRow = displayRows.get(key);
                        if (displayRow == null) {
                            displayRow = new String[5];
                            if (!key.equals("")) {
                                displayRow[0] = repo.getName()+"-"+key;
                            } else {
                                displayRow[0] = repo.getName();
                            }
                            displayRows.put(key, displayRow);
                        }
                        if (dbRow[0].startsWith("incomingNewRecordsCount")) {
                            displayRow[1] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("incomingUpdatedRecordsCount")) {
                            displayRow[2] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("incomingDeletedRecordsCount")) {
                            displayRow[3] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("incomingProcessingErrorsCount")) {
                            displayRow[4] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        }
                    }
                }
                for (String[] displayRow : displayRows.values()) {
                    table.add(displayRow);
                }
            }
        }
        return table;
    }

    public List<String[]> getOutgoingServiceRecords() {
        List<String[]> table = new ArrayList<String[]>();
        table.add(new String[] {"service_name-type", "active records", "updates", "deleted records"});
        List<Repository> repos = getAll();
        for (Repository repo : repos) {
            if (repo.ready4harvest() && repo.getService() != null) {
                List<String[]> dbRows = getRepositoryDAO().getAllPersistentProperties(repo.getName());
                Map<String, String[]> displayRows = new TreeMap<String, String[]>();
                if (dbRows != null) {
                    for (String[] dbRow : dbRows) {
                        if (!dbRow[0].startsWith("outgoing")) {
                            continue;
                        }
                        int idx0 = dbRow[0].indexOf("-");
                        String key = "";
                        if (idx0 > -1) {
                            key = dbRow[0].substring(idx0+1);
                        }
                        String[] displayRow = displayRows.get(key);
                        if (displayRow == null) {
                            displayRow = new String[4];
                            if (!key.equals("")) {
                                displayRow[0] = repo.getName()+"-"+key;
                            } else {
                                displayRow[0] = repo.getName();
                            }
                            displayRows.put(key, displayRow);
                        }
                        if (dbRow[0].startsWith("outgoingActiveRecordsCount")) {
                            displayRow[1] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("outgoingUpdatedRecordsCount")) {
                            displayRow[2] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        } else if (dbRow[0].startsWith("outgoingDeletedRecordsCount")) {
                            displayRow[3] = String.format(FORMAT_INTEGRALS, Long.parseLong(dbRow[1]));
                        }
                    }
                }
                for (String[] displayRow : displayRows.values()) {
                    table.add(displayRow);
                }
            }
        }
        return table;
    }

}
