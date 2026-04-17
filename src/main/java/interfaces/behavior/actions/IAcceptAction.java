package interfaces.behavior.actions;

// OUTDATED
public interface IAcceptAction {
    // ActionUsage's name. (ex: "TurnOn")
	public String getName();

    // Payload's name (ex: "TurnOn")
	public String getPayloadName();

    // Recipient's name, if any (e.g., "battery")
	public String getReceiverName();
}
