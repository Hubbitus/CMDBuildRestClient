package expteam.common.clients

import groovyx.net.http.Method
import org.apache.http.client.ClientProtocolException
import spock.lang.Shared
import spock.lang.Specification

//@Grapes([
//	@Grab(group='org.spockframework', module='spock-core', version='1.0-groovy-2.4'),
//	@Grab(group='junit', module='junit', version='4.12')
//])

/**
 *
 *
 * @author Pavel Alexeev - <Pahan@Hubbitus.info>
 * @created 27-04-2015 11:31 AM
 **/
class CMDBuildClientTest extends Specification {
	@Shared CMDBuildClient client = new CMDBuildClient('http://192.168.100.206:8080/cmdbuild/services/rest/v2/', 'admin', 'test');

	def "Auth"(){
		CMDBuildClient badClient = new CMDBuildClient('http://192.168.100.206:8080/cmdbuild/services/rest/v2/', 'admin', 'WRONG PASS');
		when:
			badClient.get(path: 'lookup_types')
		then:
			thrown(ClientProtocolException)

		when:
			client.get(path: 'lookup_types')
		then:
			notThrown(ClientProtocolException)
			client.sessionId
	}

	def "Query"() {
		when:
			def res = client.query(Method.GET, [path: 'lookup_types'])
		then:
			res.data
			res.data.data.size() > 0
			res.data.data.size() == res.data.meta.total
	}

	def "Get"() {
		when:
			def res = client.get(path: 'lookup_types')
		then:
			res.data
			res.data.data.size() > 0
			res.data.data.size() == res.data.meta.total
	}

	def "Get with advanced pagination and filtering"(){
		expect:
			res.data
			res.data.data.size() == 10
			res.data.meta.total >= res.data.data.size()

		where:
			res << [
				client.get( // filterStringSimple
					path: 'classes/Node/cards/'
					,query: [
						limit: 10
						,start: 10
						// Variant with JSON-string filter: ESB servers
						,filter: '{"attribute":{"simple":{"value":[23],"attribute":"nodeType","parameterType":"fixed","operator":"equal"}}}'
					]
				)
				,client.get( // filterStructured
					path: 'classes/Node/cards/'
					,query: [
						limit: 10
						,start: 10
						,filter: [
							attribute:[
								simple: [
									attribute: 'nodeType'
									,operator: 'equal'
									,value: [23]
									,parameterType: 'fixed'
								]
							]
						]
					]
				)
			]
	}

// Post used in Auth and mostly destructive, so do not test explicit
//	def "Post"() {
//
//	}
}
