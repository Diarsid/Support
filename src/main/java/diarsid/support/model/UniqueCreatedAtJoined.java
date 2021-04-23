package diarsid.support.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

public class UniqueCreatedAtJoined<L, R> implements Unique, CreatedAt, Joined<L, R> {

    private final UUID uuid;
    private final LocalDateTime createdAt;
    private final L left;
    private final R right;

    public UniqueCreatedAtJoined(L left, R right) {
        this.uuid = randomUUID();
        this.createdAt = now();
        this.left = left;
        this.right = right;
    }

    public UniqueCreatedAtJoined(UUID uuid, LocalDateTime createdAt, L left, R right) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.left = left;
        this.right = right;
    }

    @Override
    public LocalDateTime createdAt() {
        return createdAt;
    }

    @Override
    public L left() {
        return left;
    }

    @Override
    public R right() {
        return right;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueCreatedAtJoined)) return false;
        UniqueCreatedAtJoined<?, ?> that = (UniqueCreatedAtJoined<?, ?>) o;
        return uuid.equals(that.uuid) &&
                createdAt.equals(that.createdAt) &&
                left.equals(that.left) &&
                right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, createdAt, left, right);
    }
}
