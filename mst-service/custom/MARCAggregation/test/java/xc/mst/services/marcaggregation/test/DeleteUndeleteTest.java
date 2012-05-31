package xc.mst.services.marcaggregation.test;

import java.util.ArrayList;
import java.util.List;

/**
 * delete a record and then readd it.  results should end up same as small test, because that is our base,
 * and that is what we end up with after delete/readd.
 *
 * @author John Brand
 *
 */
public class DeleteUndeleteTest extends SmallTest {

    public List<String> getFolders() {
        List<String> fileStrs = new ArrayList<String>();
        fileStrs.add("delete_1_undelete");
        return fileStrs;
    }

    protected String getTestName() {
        return "DeleteUndeleteTest";
    }

}
