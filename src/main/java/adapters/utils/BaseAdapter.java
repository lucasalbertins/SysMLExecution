package adapters.utils;

import org.omg.sysml.lang.sysml.Element;
import interfaces.utils.IElement;

public abstract class BaseAdapter implements IElement {

    protected final Element element;

    public BaseAdapter(Element element) {
        this.element = element;
    }

    @Override
    public String getID() {
        try {
            return element != null && element.getElementId() != null
                    ? element.getElementId()
                    : "<no-id>";
        } catch (Exception e) {
            return "<error-getting-id>";
        }
    }
}
