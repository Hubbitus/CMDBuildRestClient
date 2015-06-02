package expteam.common.clients

import groovy.transform.TupleConstructor
//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='[0.7,)')
import groovyx.net.http.RESTClient
import groovyx.net.http.Method

import static groovyx.net.http.ContentType.JSON

/**
 * Basic CMDBuild groovy client.
 * Example of usage:
 *
 * CMDBuildClient client = new CMDBuildClient('http://192.168.100.183:8280/cmdbuild/services/rest/v1/', 'login', 'password');
 * def res = client.get(path: 'lookup_types')
 * println "Operation status: ${res.status}"
 * println "Operation return data: ${res.data}"
 * …
 * client.get('classes')
 * client.post(…)
 *
 * Advanced filtering and limiting supported:
 * def res = client.get(
 *	path: 'classes/Node/cards/'
 * ,query: [
 *		limit: 100 // 100 elements
 *		,start: 10 // Starting from 10 (pagination)
 * //	Text of JSON supported
 * //	,filter: '{"attribute":{"simple":{"attribute":"databaseType","operator":"equal","value":[470],"parameterType":"fixed"}}}'
 * //	Or may be built on the fly:
 *		,filter: [
 *			attribute:[
 *				simple: [
 *					attribute: 'databaseType'
 *					,operator: 'equal'
 *					,value: [470]
 *					,parameterType: 'fixed'
 *				]
 *			]
 *		]
 *		// RGC with MSSQL
 *		,filter: '{"attribute":{"and":[{"simple":{"value":[472],"attribute":"databaseType","parameterType":"fixed","operator":"equal"}},{"simple":{"value":[24],"attribute":"nodeType","parameterType":"fixed","operator":"equal"}}]}}'
 *	]
 * );
 *
 * Unfortunately there no possibility use CQL like HQL, so many requests needed, even for lookups take by name:
 * See:
 * http://www.cmdbuild.org/forum/forum-in-english/430046613?set_language=en&cl=en
 * http://www.cmdbuild.org/forum/forum-in-english/304195182
 * http://www.cmdbuild.org/forum/forum-in-english/909278360/view
 *
 * @author Pavel Alexeev <Pahan@Hubbitus.info>
 * @created 27.04.2015 13:51:50
 **/
@TupleConstructor
class CMDBuildClient{
	String url
	String user
	String pass

	private @Lazy String sessionId = auth();
	/**
	 * 	http://192.168.100.183:8280/cmdbuild/services/rest/v1/ -> "cmdbuild/services/rest/v1/" constant, so omit
 	 */
	private @Lazy String baseUriPath = new URL(url).path;

	private @Lazy RESTClient client = new RESTClient(url, JSON);

	/**
	* Authenticate with given user/pass and store sessionId (and return it)
	*
	* @TODO add exceptions handling
	* @return got sessionId (resp.data.data._id)
	*/
	private String auth(){
		def resp = client.post(
			path: baseUriPath + 'sessions',
			body: [ username: user, password: pass ]
		)
		resp.data.data._id;
	}

	/**
	 * Base query service method
	 *
	 * You do not need pass any authentication data or headers - thy are handled automatically.
	 * But you must check exceptions and status codes
	 *
	 * @param params named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
	 *	In most cases only path required. If it starts from / - treated as absolute and used as is. Otherwise prepended
	 *	by {@see baseUriPath}
	 */
	def query(Method method, Map params){
		if (!params.headers){
			params.headers = [:]
		}
		params.headers << [
			'CMDBuild-Authorization': sessionId
		];

		if ('/' != params.path[0])
			params.path = baseUriPath + params.path;

		client."${method.name().toLowerCase()}"(params);
	}

	/**
	 * Convenient method to perform GET requests
	 *
	 * @param params named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
	 * @return
	 */
	def get(Map params){
		query(Method.GET, params)
	}

	/**
	 * Convenient method to perform GET requests
	 *
	 * @param params named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
	 * @return
	 */
	def post(Map params){
		query(Method.POST, params)
	}
}
