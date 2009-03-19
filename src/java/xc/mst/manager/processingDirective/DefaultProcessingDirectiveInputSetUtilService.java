

package xc.mst.manager.processingDirective;

import java.util.List;

import xc.mst.dao.processing.DefaultProcessingDirectiveInputSetUtilDAO;
import xc.mst.dao.processing.ProcessingDirectiveInputSetUtilDAO;

/**
 *
 * @author tejaswih
 */
public class DefaultProcessingDirectiveInputSetUtilService implements ProcessingDirectiveInputSetUtilService
{

    ProcessingDirectiveInputSetUtilDAO pdisUtilDao;

    public DefaultProcessingDirectiveInputSetUtilService()
    {
        pdisUtilDao = new DefaultProcessingDirectiveInputSetUtilDAO();
    }
	public void insertPDIS(int processingDirectiveId, int setId)
    {
        pdisUtilDao.insert(processingDirectiveId, setId);
    }

	public void deletePDIS(int processingDirectiveId, int setId)
    {
        pdisUtilDao.delete(processingDirectiveId, setId);
    }

	public  List<Integer> getInputSetsForProcessingDirective(int processingDirectiveId)
    {
        return pdisUtilDao.getInputSetsForProcessingDirective(processingDirectiveId);
    }

	public void deleteInputSetsForProcessingDirective(int processingDirectiveId)
    {
        pdisUtilDao.deleteInputSetsForProcessingDirective(processingDirectiveId);
    }
}
