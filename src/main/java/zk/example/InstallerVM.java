package zk.example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Messagebox;
import org.zkoss.zuti.zul.NavigationLevel;
import org.zkoss.zuti.zul.NavigationModel;

public class InstallerVM {
	private static final String STEP_FOLDER = "/WEB-INF/zul/";

	private NavigationModel<Step> navModel;
	private List<String> keys;
	private Component comp;
	private InstallerField fields;

	@Init
	public void init(@ContextParam(ContextType.COMPONENT) Component comp) {
		keys = Arrays.asList("Welcome", "EULA", "Destination", "Items", "Config", "Ready", "Installing", "Complete");

		navModel = new NavigationModel<>();
		navModel.put("Welcome", newStep("welcome", "Welcome"));
		navModel.put("EULA", newStep("eula", "EULA").withBeforeNextHandler(() -> {
			boolean acceptEula = Boolean.TRUE.equals(fields.get("eula"));
			return acceptEula ? "" : "You must accept the EULA to continue.";
		}));
		navModel.put("EULA/01", newStep("eula-1", "EULA/01"));
		navModel.put("EULA/02", newStep("eula-2", "EULA/02"));
		navModel.put("Destination", newStep("dest", "Destination"));
		navModel.put("Items", newStep("items", "Items"));
		navModel.put("Config", newStep("config", "Config"));
		navModel.put("Ready", newStep("ready", "Ready").withNextLabel("Install"));
		navModel.put("Installing", newStep("installing", "Installing"));
		navModel.put("Complete", newStep("complete", "Complete").withNextLabel("Finish"));

		fields = new InstallerField();

		this.comp = comp;
	}

	private Step newStep(String id, String title) {
		return new Step(id, title, STEP_FOLDER + id + ".zul");
	}

	public NavigationModel<Step> getNavModel() {
		return navModel;
	}

	public NavigationLevel<Step> getNavLevel() {
		return navModel.getRoot();
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public int getCurrentIndex() {
		String currentKey = getNavLevel().getCurrentKey();
		return keys.indexOf(currentKey);
	}

	@DependsOn("currentIndex")
	public boolean isFirst() {
		return getCurrentIndex() == 0;
	}

	@DependsOn("currentIndex")
	public boolean isLast() {
		return getCurrentIndex() == keys.size() - 1;
	}

	@Command
	@NotifyChange("currentIndex")
	public void back() {
		int index = getCurrentIndex();
		if (index > 0) {
			navModel.navigateTo(keys.get(index - 1));
		}
	}

	@Command
	public void next() {
		int index = getCurrentIndex();
		if (index != -1 && index < keys.size() - 1) {
			String errorMessage = getNavLevel().getCurrent().onBeforeNext();
			if (Strings.isEmpty(errorMessage)) {
				navModel.navigateTo(keys.get(index + 1));
				BindUtils.postNotifyChange(null, null, this, "currentIndex");
			} else
				Messagebox.show(errorMessage);
		}
	}

	@Command
	public void cancel() {
		comp.detach();
	}

	@Command
	public void finish() {
		comp.detach();
	}
}
