package fedilnik.android.data;

public interface DataStoreCallback {
    void dataDidChange(Status status);
    void dataDidStartRefreshing();
}
