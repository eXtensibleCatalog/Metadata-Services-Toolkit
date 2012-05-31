/**
 * Copyright (c) 2010 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */
package xc.mst.utils;

import java.io.IOException;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.UpdateHandler;

public class OptimizeSolrIndex {
   private String solrCoreDir;
   private String solrDataDir;
   private SolrCore solrCore;
   private SolrConfig solrConfig;
   private IndexSchema solrIndexSchema;
   private UpdateHandler updateHandler;

   public OptimizeSolrIndex(String solrCoreDir, String solrDataDir) throws IOException
   {
      try {
         this.solrCoreDir = solrCoreDir;
         this.solrDataDir = solrDataDir;
         System.setProperty("solr.home", solrCoreDir);
         solrConfig = new SolrConfig(solrCoreDir, "solrconfig.xml", null);
         solrIndexSchema = new IndexSchema(solrConfig, "schema.xml", null);
         solrCore = new SolrCore(solrDataDir, solrIndexSchema);
      } catch (Exception e) {
         System.err.println("Couldn't set the instance directory");
         e.printStackTrace();
         System.exit(1);
      }
      updateHandler = solrCore.getUpdateHandler();
   }

   public void commit(boolean optimize) throws IOException {
      CommitUpdateCommand commitCmd = new CommitUpdateCommand(optimize);
      updateHandler.commit(commitCmd);
   }

   public static void main(String[] args) 
   {
      OptimizeSolrIndex theIndex = null;
      try {
            theIndex = new OptimizeSolrIndex(args[0], args[1]);
            theIndex.commit(true);
      } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
      }

      System.exit(0);
   }
}
