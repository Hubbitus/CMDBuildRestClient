Groovy class implementing [CMDBuild](http://www.cmdbuild.org/) [REST API](http://www.cmdbuild.org/file/manuali/webservice-manual-in-english).

For examples of usage see groovy-doc of class and tests.

Simple usage example:
```
	client = new CMDBuildClient('http://192.168.100.206:8080/cmdbuild/services/rest/v2/', 'Login', 'Password');
	def res client.get(path: '')

	println "Operation status: ${res.status}"
	println "Operation return data: ${res.data}"

	assert client.get(path: 'classes').data
	assert client.get(path: 'classes/Host').data
	assert client.get(path: 'classes/Host/cards').date

	assert client.get(path: 'classes/MonObject').data
	assert client.get(path: 'classes/MonObject').data.data
	assert client.get(path: 'classes/MonObject').data.meta
	assert client.get(path: 'classes/MonObject').data.meta.total

	assert client.get(path: 'classes/MonObject/cards').data
	assert client.get(path: 'classes/MonObject/cards/243') // stavropolrg-db
```