package xc.mst.dao.record;

import java.util.List;

import org.apache.log4j.Logger;

import xc.mst.bo.record.RecordType;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.MySqlConnectionManager;

/**
 *  Data Access Object for the record types table
 * @author vinaykumarb
 *
 */
public abstract class RecordTypeDAO {

	/**
	 * A reference to the logger for this class
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);
	
	/**
	 * The Object managing the database connection
	 */
	protected MySqlConnectionManager dbConnectionManager = MySqlConnectionManager.getInstance();

	/**
	 * The name of the database table we're interacting with
	 */
	protected final static String RECORD_TYPES_TABLE_NAME = "record_types";

	/**
	 * The name of the record type ID column
	 */
	protected final static String COL_ID = "id";
	
	/**
	 * The name of the record type name column
	 */
	protected final static String COL_NAME = "name";
	
	/**
	 * The name of the processing order column
	 */
	protected final static String COL_PROCESSING_ORDER = "processing_order";
	
	
	/**
	 * Gets all record types in the database
	 *
	 * @return A list of all record types in the database
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract List<RecordType> getAll() throws DatabaseConfigException;

	/**
	 * Gets the record type from the database with the passed ID
	 *
	 * @param id The ID of the record type to get
	 * @return The record type with the passed ID
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract RecordType getById(long id) throws DatabaseConfigException;
	

	/**
	 * Gets the record type from the database with the passed processing order
	 *
	 * @param token The processing order of the record type to get
	 * @return The record type with the passed processing order
	 * @throws DatabaseConfigException if there was a problem connecting to the database
	 */
	public abstract RecordType getByPorcessingOrder(int processingOrder) throws DatabaseConfigException;

	/**
	 * Inserts a record type from the database
	 *
	 * @param recordType The record type to insert
	 * @return True if the record type was inserted successfully, false otherwise
	 * @throws DataException If the fields on the passed record type were not valid for inserting
	 */
	public abstract boolean insert(RecordType recordType) throws DataException;

	/**
	 * Updates a record type in the database
	 *
	 * @param recordType The record type to update
	 * @return True if the record type was updated successfully, false otherwise
	 * @throws DataException If the fields on the passed record type were not valid for updating
	 */
	public abstract boolean update(RecordType recordType) throws DataException;

	/**
	 * Deletes a record type from the database
	 *
	 * @param recordType The record type to delete
	 * @return True if the record type was deleted successfully, false otherwise
	 * @throws DataException If the fields on the passed resumption token were not valid for deleting
	 */
	public abstract boolean delete(RecordType recordType) throws DataException;

}
