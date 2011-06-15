This plugin enables you to create a Grails project that utilizes the ATG Core 
as part of your ATG application. 

## Dependencies

The plugin sets up the [ATG Ivy Resolver][atg-ivy-resolver] for you, so you can
just declare a dependency on your module and all your module's dependencies will 
be available to `grails` (and can be added to your IDE):

	provided( [group:'ATG_MODULE',name:'Musicstore',version:'SNAPSHOT',type:'ivy'] )

## Injecting Nucleus Components

The plugin provides a factory bean to make loading Nucleus components easy:

	import org.grails.atg.nucleus.NucleusFactory

	beans = {
		artistRepo(NucleusFactory,'/musicstore/SongsRepository')
	}

You can then inject the component into your controllers, services and even domain classes:

	class ArtistController {
		
		ContentRepository artistRepo
		
		def list = {
			def view = artistRepo.getView("artist")
			Query q = view.queryBuilder.createUnconstrainedQuery()
			[artists:view.executeQuery(q)]
		}
		
	}

## DSP Tags

Ideally, you can use dependency injection to access Nucleus components so you 
wont have to use DSP tags. However, the full DSP tag library can be used in your GSPs if you need it:

	<%@ taglib prefix="dsp" uri="/dspTaglib" %>
	<dsp:page>
		...

[atg-ivy-resolver]: https://github.com/iamnoah/atg-ivy-resolver
