//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

File resolveAtgRoot() {
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
	root
}

def atgRoot = resolveAtgRoot()

if(atgRoot) {
	ant.copy(file:"${atgRoot.absolutePath}/DAS/taglib/dspjspTaglib/1.0/tld/dspjspTaglib1_0.tld",
			todir:"${basedir}/web-app/WEB-INF/tld/")
} else {
	println "ERROR - Neither ATG_HOME or DYNAMO_HOME were " +
	"defined and your project doesn't appear to be in an ATG module.\n\n" +
	"You will need to manually copy <ATG_ROOT>/DAS/taglib/dspjspTaglib/1.0/tld/dspjspTaglib1_0.tld into WEB-INF/tld/ to use DSP tags."
}