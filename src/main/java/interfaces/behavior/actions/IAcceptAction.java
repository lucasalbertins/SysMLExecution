package interfaces.behavior.actions;

public interface IAcceptAction {
    // nome da action usage (ex: “TurnOn”)
	public String getName();

    // nome do payload (ex: “TurnOn”)
	public String getPayloadName();

    // nome do receptor, se houver (ex: “battery”)
	public String getReceiverName();
}