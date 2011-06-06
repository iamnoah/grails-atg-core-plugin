import java.io.File;
import java.util.List;

import org.apache.ivy.plugins.repository.AbstractRepository;
import org.apache.ivy.plugins.repository.BasicResource;
import org.apache.ivy.plugins.repository.Resource;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.apache.ivy.plugins.resolver.RepositoryResolver;
import org.apache.ivy.plugins.resolver.packager.BuiltFileResource;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	def repo = new AbstractRepository() {
		
		final ORG = 'ATG-MODULE'
		
		File atg_root
		
		File default_jar
		
		File getAtgRoot() {
			atg_root = atg_root ?: resolveAtgRoot() ?: new File(".").absoluteFile
		}
		
		File getDefaultJar() {
			default_jar = default_jar ?: createDefaultJar()
		}
		
		
		protected File createDefaultJar() {
			Jar task = new Jar()
			def defaultManifest = File.createTempFile("atg-default",".mf")
			Manifest.defaultManifest.print(defaultManifest.newPrintWriter())
			task.manifest = defaultManifest
			def defaultJar = File.createTempFile("atg-default",".jar")
			task.destFile = defaultJar
			task.execute()
			defaultJar
		}
		
		protected File resolveAtgRoot() {
			def root = System.getenv("DYNAMO_HOME") ?: System.getenv("ATG_HOME")
			if(root) {
				root = new File(root).parentFile
			}  else {
				root = new File(".").absoluteFile
				while(root && !new File(root,"home").with { 
					it.directory && it.exists()
				}) {
					root = root.parentFile
				}
			}
			println "ATG root dir is ${root.absolutePath}"
			root
		}
		
		@Override
		public void get(String src, File dest) throws IOException {
			def match = src =~ /atg-(.+?)-.*-ivy\.xml/
			def r 
			if(match) {
				r = getResource(match[0][1])
			} else {
				match = src =~ /([^\/]+)\/lib\/classes\.jar$/
				if(match) {
					r = getResource("${ORG}:${match[0][1]}:any:jar")
				} else {
					r = getResource('default-jar')
				}
			}
			fireTransferInitiated(r,5)
			
			new URL(src).withInputStream { ins ->
				dest.withOutputStream { out -> out << ins }
			}
			fireTransferCompleted()
		}
		
		@Override
		public Resource getResource(String src) throws IOException {
			if(src == 'default-jar') {
				new BuiltFileResource(defaultJar)
			}
			def (org,module,ver,type) = (src.split(/:/) as List)
			if(org != ORG) {
				return new BasicResource('not found',false,0,0,false)
			}
			File file = new File(atgRoot,"${module}/META-INF/MANIFEST.MF")
			if(file.exists() && type == "ivy") {
				File tmp = File.createTempFile("atg-${src}-","-ivy.xml")
				tmp.withWriter {
					def build = new groovy.xml.MarkupBuilder(it)
					build.doubleQuotes = true
					build.'ivy-module'(version:'2.0') {
						info(
								organisation: org,
								module: module,
								revision: ver,
								status: 'release',
								'default': 'true',
								)
						build.dependencies {
							file.withReader { 
								Manifest m = new Manifest(it)
								def val = m.getMainSection().getAttributeValue("ATG-Required")
								val?.split(/\s+/)?.findAll { it }?.each {
									build.dependency(org:ORG,name:it,rev:ver)
								}
							}
						}
						build.configurations() {
							conf(name:"default",visibility:"public")
						}
						build.publications() {
							artifact(name:module,type:"jar",ext:"jar",conf:"default")
						}
					}
					it.close()
				}
				new BuiltFileResource(tmp)
			} else if(file.exists()){
				File jar = new File(atgRoot,"${module}/lib/classes.jar")
				if(jar.exists()) {
					new BuiltFileResource(jar)
				} else {
					new BuiltFileResource(defaultJar)
				}
			}  else {
				new BuiltFileResource(file)
			}
		}
		
		@Override
		public List<?> list(String parent) throws IOException {
			return Collections.emptyList();
		}
	}
	
	def atgResolver = new RepositoryResolver()
	atgResolver.repository = repo
	atgResolver.addIvyPattern("[organisation]:[module]:[revision]:[type]:[artifact]:[ext]")
	atgResolver.addArtifactPattern("[organisation]:[module]:[revision]:[type]:[artifact]:[ext]")
	atgResolver.name = "ATG Resolver"
	resolver atgResolver
	
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
		provided( [group:'ATG-MODULE',name:'DAS',version:'SNAPSHOT'] )
    }
}
