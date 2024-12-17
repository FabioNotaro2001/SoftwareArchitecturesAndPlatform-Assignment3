import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass2.ebikes.domain.Ebike;
import sap.ass2.ebikes.domain.P2d;
import sap.ass2.ebikes.domain.V2d;

public class EbikeTest {
    private Ebike ebike;

    @BeforeEach
    void setUp() {
        ebike = new Ebike("bike001");
    }

    @Test
    void testConstructorDefaultState() {
        assertEquals("bike001", ebike.getId());
        assertEquals(Ebike.EbikeState.AVAILABLE, ebike.getState());
        assertEquals(100, ebike.getBatteryLevel());
        assertEquals(new P2d(0, 0).toString(), ebike.getLocation().toString());
        assertEquals(new V2d(1, 0).toString(), ebike.getDirection().toString());
        assertEquals(0, ebike.getSpeed());
    }

    @Test
    void testCustomConstructor() {
        P2d initialPosition = new P2d(10, 20);
        Ebike customEbike = new Ebike("bike002", initialPosition);

        assertEquals("bike002", customEbike.getId());
        assertEquals(Ebike.EbikeState.AVAILABLE, customEbike.getState());
        assertEquals(100, customEbike.getBatteryLevel());
        assertEquals(initialPosition.toString(), customEbike.getLocation().toString());
    }

    @Test
    void testSetBatteryLevel() {
        ebike.setBatteryLevel(80);
        assertEquals(80, ebike.getBatteryLevel());
        assertEquals(Ebike.EbikeState.AVAILABLE, ebike.getState());

        ebike.setBatteryLevel(0);
        assertEquals(0, ebike.getBatteryLevel());
        assertEquals(Ebike.EbikeState.MAINTENANCE, ebike.getState());

        ebike.setBatteryLevel(150); 
        assertEquals(100, ebike.getBatteryLevel());

        ebike.setBatteryLevel(-10); 
        assertEquals(0, ebike.getBatteryLevel());
    }

    @Test
    void testDecreaseBatteryLevel() {
        ebike.decreaseBatteryLevel(30);
        assertEquals(70, ebike.getBatteryLevel());
        assertEquals(Ebike.EbikeState.AVAILABLE, ebike.getState());

        ebike.decreaseBatteryLevel(100);
        assertEquals(0, ebike.getBatteryLevel());
        assertEquals(Ebike.EbikeState.MAINTENANCE, ebike.getState());
    }

    @Test
    void testRechargeBattery() {
        ebike.setBatteryLevel(10);
        ebike.rechargeBattery();

        assertEquals(100, ebike.getBatteryLevel());
        assertEquals(Ebike.EbikeState.AVAILABLE, ebike.getState());
    }

    @Test
    void testUpdateState() {
        ebike.updateState(Ebike.EbikeState.IN_USE);
        assertEquals(Ebike.EbikeState.IN_USE, ebike.getState());

        ebike.updateState(Ebike.EbikeState.DISMISSED);
        assertEquals(Ebike.EbikeState.DISMISSED, ebike.getState());
    }

    @Test
    void testUpdateLocation() {
        P2d newPosition = new P2d(50, 50);
        ebike.updateLocation(newPosition);

        assertEquals(newPosition.toString(), ebike.getLocation().toString());
    }

    @Test
    void testUpdateSpeed() {
        ebike.updateSpeed(20.5);
        assertEquals(20.5, ebike.getSpeed());
    }

    @Test
    void testUpdateDirection() {
        V2d newDirection = new V2d(0, 1);
        ebike.updateDirection(newDirection);

        assertEquals(newDirection.toString(), ebike.getDirection().toString());
    }

    @Test
    void testIsAvailable() {
        assertTrue(ebike.isAvailable());

        ebike.updateState(Ebike.EbikeState.IN_USE);
        assertFalse(ebike.isAvailable());
    }

    @Test
    void testIsInUse() {
        assertFalse(ebike.isInUse());

        ebike.updateState(Ebike.EbikeState.IN_USE);
        assertTrue(ebike.isInUse());
    }

    @Test
    void testToString() {
        String expected = "{ id: bike001, loc: P2d(0.0,0.0), batteryLevel: 100, state: AVAILABLE }";
        assertEquals(expected, ebike.toString());
    }
}
