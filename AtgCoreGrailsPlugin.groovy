import atg.repository.RepositoryItem;

class AtgCoreGrailsPlugin {
	// the plugin version
	def version = "0.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "1.3.6 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]
	
	def author = "Noah Sloan"
	def authorEmail = ""
	def title = "ATG Core Grails Plugin"
	def description = '''\\
Plugin for creating Grails applications that utilize the ATG Core.
e.g., Nucleus, Repositories, and the DSP taglib.   
'''
	
	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/atg-core"
	
	def doWithWebDescriptor = { xml ->
		
		(xml.'context-param')[0] + {
			'context-param' {
				'param-name'('atg.filter.PageFilterDebug')
				'param-value'('true')
			}
		}
		(xml.'filter')[0] + {
			'filter' {
				'filter-name'('PageFilter')
				'filter-class'('atg.filter.dspjsp.PageFilter')
			}
			'filter-mapping' {
				'filter-name'('PageFilter')
				'url-pattern'("*.jsp")
				// TODO Should this map to *.gsp too?
			}
		}
		(xml.'servlet')[0] + {
			'servlet' {
				'servlet-name'('NucleusServlet')
				'servlet-class'('atg.nucleus.servlet.NucleusServlet')
				'load-on-startup'('1')
			}
			
			'servlet' {
				'servlet-name'('AdminProxyServlet')
				'servlet-class'('atg.nucleus.servlet.NucleusProxyServlet')
				'init-param' {
					'param-name'('proxyServletPath')
					'param-value'('/atg/dynamo/servlet/adminpipeline/AdminHandler')
				}
				'load-on-startup'('2')
			}
			
			'servlet' {
				'servlet-name'('DynamoProxyServlet')
				'servlet-class'('atg.nucleus.servlet.NucleusProxyServlet')
				'load-on-startup'('2')
			}
			
			'servlet' {
				'servlet-name'('SessionNameContextServlet')
				'servlet-class'('atg.nucleus.servlet.SessionNameContextServlet')
			}
			
			'servlet' {
				'servlet-name'('InitSessionServlet')
				'servlet-class'('atg.nucleus.servlet.InitSessionServlet')
			}
			
			'servlet-mapping' {
				'servlet-name'('AdminProxyServlet')
				'url-pattern'('/admin/*')
			}
			
			'servlet-mapping' {
				'servlet-name'('DynamoProxyServlet')
				'url-pattern'('/dyn/*')
			}
			
			'servlet-mapping' {
				'servlet-name'('InitSessionServlet')
				'url-pattern'('/init-session')
			}
		}
		(xml.'jsp-config'.'taglib')[0] + {
			'taglib' {
				'taglib-uri'('/dspTaglib')
				'taglib-location'('/WEB-INF/tld/dspjspTaglib1_0.tld')
			}
		}
	}
	
	def doWithSpring = {
		// TODO Implement runtime spring config (optional)
	}
	
	def doWithDynamicMethods = { ctx ->
		RepositoryItem.metaClass.getProperty = { String s ->
			delegate.getPropertyValue(s)
		}
	}
	
	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}
	
	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}
	
	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}
}
