package zk.example;

import java.util.function.Supplier;

public class Step {
	private String id;
	private String title;
	private String templateUri;
	private String nextLabel = "Next";
	private Supplier<String> beforeNext = () -> "";

	public Step(String id, String title, String templateUri) {
		this.id = id;
		this.title = title;
		this.templateUri = templateUri;
	}

	public Step withNextLabel(String nextLabel) {
		this.nextLabel = nextLabel;
		return this;
	}

	public Step withBeforeNextHandler(Supplier<String> beforeNextHandler) {
		beforeNext = beforeNextHandler;
		return this;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getTemplateUri() {
		return templateUri;
	}

	public String getNextLabel() {
		return nextLabel;
	}

	public String onBeforeNext() {
		return beforeNext.get();
	}
}
