package jetty_osgi;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component
public class SimpleLoggingComponent {
	
	private SimpleLogService simpleLogService;
	
	@Reference
    public void bindLogger(SimpleLogService logService) {
		this.simpleLogService = logService;
    }

    public void unbindLogger(SimpleLogService logService) {
		this.simpleLogService = null;
    }

    @Activate
	public void activate() {
		if (simpleLogService != null) {
			simpleLogService.log("Yee ha, I'm logging!");
		}
	}
	
    @Deactivate
	public void deactivate() {
		if (simpleLogService != null) {
			simpleLogService.log("Yee ha, I'm logging!");
		}
	}
}
