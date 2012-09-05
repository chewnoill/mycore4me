mycore4me

=========


Server Side
*[core_post](/src/core_dos/server/core_post)
	*standard Http connection manager
	*custom cookie manager
	*handles all core.meditech.com network access
	*returns an instance of [JsEventList](/src/core_dos/shared/JsEventList)
*[core_parser](/src/core_dos/server/core_parser)
	*static helper functions
	*parse event information from the 3 different page styles used at core.meditech.com
*[google_post](/src/core_dos/server/google_post)
	*receives an instance of [JsEventList](/src/core_dos/shared/JsEventList)
	*handles creating Authentication headers for requests to google
	*finds or creates a "Core" calendar in google
	*receive a list of events that have already been sent to google
	*posts new events only to google
*[registry_server](/src/core_dos/server/registry_server)
	*has OnCreate function for server
	*handles communication with the client
	*controls access to above post functions

Client Side
*[Core_dos](/src/core_dos/client/Core_dos)
	*main user entrypoint
	*creates ui handles workflow
