/**
 * 
 */
package org.soma.tleaf.dao;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soma.tleaf.couchdb.CouchDbConn;
import org.soma.tleaf.domain.RawData;
import org.soma.tleaf.domain.RequestParameter;

/**
 * Created with Eclipse IDE
 * Author : RichardJ
 * Date : Oct 17, 2014 6:14:35 PM
 * Description :
 */
public class RestApiDaoImple implements RestApiDao {
	private Logger logger = LoggerFactory.getLogger(RestApiDaoImple.class);
	private static String ACCESSKEYS_DB_NAME = "tleaf_apikey";

	@Inject
	private CouchDbConn connector;

	/**
	 * Author : RichardJ
	 * Date : Oct 22, 2014 10:28:35 PM
	 * Description : 클라이언트로부터 받은 데이터를 디비에 저장합니다.
	 */
	@Override
	public void postData(Map<String,Object> result, RawData rawData) throws Exception {
		CouchDbConnector db = connector.getCouchDbConnetor("user_" + rawData.getUserId());
		db.create(rawData);
		result.put("_id", rawData.getId() );
		result.put("_rev", rawData.getRevision() );
	}

	/**
	 * Author : RichardJ
	 * Date : Oct 22, 2014 10:28:35 PM
	 * Description :
	 */
	@Override
	public RawData getData(RequestParameter param) {
		RawData data = null;
		return data;
	}

	/**
	 * Author : RichardJ
	 * Date : Oct 23, 2014 9:48:52 AM
	 * Description : 해당 사용자 데이터베이스에서 전체 데이터를 읽어옵니다.
	 */
	@Override
	public List<RawData> getAllData(RequestParameter param) throws Exception {
		CouchDbConnector db = connector.getCouchDbConnetor("user_" + param.getUserHashId());
		ViewQuery query = new ViewQuery().designDocId("_design/shack").viewName("time").startKey(param.getStartKey()).endKey(param.getEndKey())
				.limit(Integer.valueOf(param.getLimit())).descending(false);

		List<RawData> rawDatas;
		try {
			rawDatas = db.queryView(query, RawData.class);
		} catch (DocumentNotFoundException e) {
			// if there were no documents to the specific query
			e.printStackTrace();
			throw e;
		}

		// For test print Code
		logger.info("" + rawDatas.size());
		for (RawData rd : rawDatas) {
			logger.info(rd.getTime());
		}
		return rawDatas;
	}

	/**
	 * Author : RichardJ
	 * Date : Oct 23, 2014 9:48:52 AM
	 * Description : 해당 사용자 데이터베이스에서 해당 앱의 아이디의 데이터만 읽어옵니다.
	 */
	@Override
	public List<RawData> getAllDataFromAppId(RequestParameter param) throws Exception {

		logger.info(param.getAppId() + " application query");
		logger.info("startKey = " + "[" + param.getAppId() + "," + param.getStartKey() + "]");
		logger.info("endKey = " + "[" + param.getAppId() + "," + param.getEndKey() + "]");

		/**
		 * 2014.10.25
		 * 
		 * @author susu
		 */

		CouchDbConnector db = connector.getCouchDbConnetor("user_" + param.getUserHashId());

		ComplexKey startKey = ComplexKey.of(param.getAppId(), param.getStartKey());
		ComplexKey endKey = ComplexKey.of(param.getAppId(), param.getEndKey());

		ViewQuery query = new ViewQuery().designDocId("_design/shack").viewName("app").startKey(startKey).endKey(endKey).descending(true);

		List<RawData> rawDatas;
		try {
			rawDatas = db.queryView(query, RawData.class);
		} catch (DocumentNotFoundException e) {
			// if there were no documents to the specific query
			e.printStackTrace();
			throw e;
		}

		return rawDatas;
	}

	/**
	 * Deletes the Specific log data.
	 * 2014.10.30
	 * @author susu
	 */
	@Override
	public void deleteData( Map<String,Object> result, RawData rawData ) throws Exception {
		
		logger.info( rawData.getUserId() );
		
		// DatabaseConnectionException Can Occur
		CouchDbConnector couchDbConnector_user = connector.getCouchDbConnetor("user_" + rawData.getUserId());
		
		// UpdateConfilct Exception??
		result.put("_rev", couchDbConnector_user.delete( rawData.getId(), rawData.getRevision() ) );
		
		logger.info( "Deleted User Log. _rev = " + (String)result.get("_rev") );

	}

	/**
	 * Updates the Specific Log data.
	 * @author susu
	 * Date Oct 30, 2014
	 * @param rawData
	 */
	@Override
	public void updateData(Map<String, Object> result, RawData rawData)
			throws Exception {
		
		logger.info( rawData.getUserId() );
		logger.info( rawData.getAppId() );
		
		// DatabaseConnectionException Can Occur
		CouchDbConnector couchDbConnector_user = connector.getCouchDbConnetor("user_" + rawData.getUserId());
		
		// UpdateConflictException
		couchDbConnector_user.update(rawData);
		
		result.put("update", "success");
		logger.info( "User Log Update Complete" );
	}

}
