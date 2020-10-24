import java.util.HashMap;
import java.util.Map;

public class DataStore {
	private static Map<String, Peer> peers = new HashMap<>();
	private static DataStore instance = new DataStore();
	
	public static DataStore getInstance(){
		return instance;
	}

	private DataStore(){
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
	
	public boolean jaRegistrado(String peerAddress){
		return peers.containsKey(peerAddress);
	}
	
}
