import model.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {


    @ParameterizedTest()
    @DisplayName("Test X set position")
    @ValueSource(ints={5})
    void setPosX(int posX) {
        Position pos = new Position(0,0);
        pos.setPos(5,8);
        assertEquals(pos.getX(), posX);
    }

    @ParameterizedTest()
    @DisplayName("Test Y set position")
    @ValueSource(ints={8})
    void setPosY(int posY) {
        Position pos = new Position(0,0);
        pos.setPos(5,8);
        assertEquals(pos.getY(), posY);
    }

    @org.junit.jupiter.api.Test
    void equals() {
        Position pos1 = new Position(4,9);
        Position pos2 = new Position(0,0);
        pos2.setPos(4,9);
        assert(pos1.equals(pos2));
    }
}