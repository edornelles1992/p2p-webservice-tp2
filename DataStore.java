import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DataStore {
	private static Map<String, Peer> peers = new HashMap<>();
	private static DataStore instance = new DataStore();
	private static Map<String, LocalDateTime> conexoes = new HashMap<>();

	public static DataStore getInstance() {
		return instance;
	}

	private DataStore() {
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

	public boolean jaRegistrado(String peerAddress) {
		return peers.containsKey(peerAddress);
	}

	public static Map<String, Peer> getPeers() {
		return peers;
	}

	public static void setPeers(Map<String, Peer> peers) {
		DataStore.peers = peers;
	}

	public static void setInstance(DataStore instance) {
		DataStore.instance = instance;
	}

	public static Map<String, LocalDateTime> getConexoes() {
		return conexoes;
	}

	public static void setConexoes(Map<String, LocalDateTime> conexoes) {
		DataStore.conexoes = conexoes;
	}

	public void putConexao(String ip) {
		conexoes.put(ip, LocalDateTime.now());
	}
}
