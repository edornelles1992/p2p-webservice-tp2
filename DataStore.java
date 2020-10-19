import java.util.HashMap;
import java.util.Map;

public class DataStore {
	private Map<String, Peer> peers = new HashMap<>();
	private static DataStore instance = new DataStore();
	
	public static DataStore getInstance(){
		return instance;
	}

	private DataStore(){
		peers.put("10.0.0.1", new Peer("10.0.0.1", "4000"));
		peers.put("10.0.0.2", new Peer("10.0.0.2", "4001"));
	}

	public Peer getPeers(String name) {
		return peers.get(name);
	}

	public void putPeers(Peer peer) {
		peers.put(peer.getIp(), peer);
	}

	@Override
	public String toString() {
		return "DataStore [peers=" + peers + "]";
	}	
	
}
