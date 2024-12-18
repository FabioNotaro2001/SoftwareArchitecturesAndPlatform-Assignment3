import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sap.ass2.ebikes.application.EbikesManagerImpl;
import sap.ass2.ebikes.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class EbikeManagerIntegrationTest {
    private EbikesManagerImpl manager; // The instance of the class under test.

    @Mock
    private EbikesRepository mockRepository; // A mock of the EbikesRepository to isolate the tests from the database.

    private Ebike ebike1; // Test instance of an Ebike.
    private Ebike ebike2; // Another test instance of an Ebike.

    @BeforeEach
    void setUp() throws RepositoryException {
        // Initialize Mockito mocks.
        MockitoAnnotations.openMocks(this);

        // Create sample Ebikes.
        ebike1 = new Ebike("ebike1", new P2d(0, 0));
        ebike2 = new Ebike("ebike2", new P2d(10, 10));

        // Mock the repository behavior: when getEbikes() is called, return a list containing the test Ebikes.
        when(mockRepository.getEbikes()).thenReturn(new ArrayList<>(List.of(ebike1, ebike2)));

        // Initialize the manager with the mocked repository.
        manager = new EbikesManagerImpl(mockRepository, null); // FIXME
    }

    @Test
    void testGetAllEbikes() {
        // Call the method under test.
        JsonArray allEbikes = manager.getAllEbikes();

        // Assert that the resulting JsonArray has the expected size.
        assertEquals(2, allEbikes.size(), "The size of the returned list of ebikes should match the number of ebikes in the repository.");

        // Verify the content of the returned JSON for the first ebike.
        JsonObject firstEbike = allEbikes.getJsonObject(0);
        assertEquals("ebike1", firstEbike.getString("ebikeId"), "The ID of the first ebike should match.");
        assertEquals(0, firstEbike.getDouble("x"), "The X location of the first ebike should match.");
        assertEquals(0, firstEbike.getDouble("y"), "The Y location of the first ebike should match.");
    }

    @Test
    void testGetAllAvailableEbikesIDs() {
        // Ensure the test scenario: both bikes are initially available.
        assertTrue(ebike1.isAvailable(), "Ebike1 should initially be available.");
        assertTrue(ebike2.isAvailable(), "Ebike2 should initially be available.");

        // Call the method under test.
        JsonArray availableEbikes = manager.getAllAvailableEbikesIDs();

        // Assert that the result contains both ebikes' IDs.
        assertEquals(2, availableEbikes.size(), "The size of the available ebikes list should match the number of available ebikes.");
        assertTrue(availableEbikes.contains("ebike1"), "The ID of ebike1 should be included in the available ebikes list.");
        assertTrue(availableEbikes.contains("ebike2"), "The ID of ebike2 should be included in the available ebikes list.");
    }

    @Test
    void testCreateEbike() throws RepositoryException {
        // Call the method under test to create a new ebike.
        JsonObject newEbike = manager.createEbike("ebike3", 20, 20);

        // Verify the content of the returned JSON object.
        assertEquals("ebike3", newEbike.getString("ebikeId"), "The new ebike's ID should match.");
        assertEquals(20, newEbike.getDouble("x"), "The X location of the new ebike should match.");
        assertEquals(20, newEbike.getDouble("y"), "The Y location of the new ebike should match.");

        // Verify that the repository's saveEbike method was called with the new ebike.
        verify(mockRepository, times(1)).saveEbikeEvent(any(EbikeEvent.class));
    }

    @Test
    void testRemoveEbike() throws RepositoryException {
        // Call the method under test to remove an ebike.
        manager.removeEbike("ebike1");

        // Verify that the repository's saveEbike method was called with the updated state (DISMISSED).
        verify(mockRepository, times(1)).saveEbikeEvent(argThat(ebike -> 
            ebike.ebikeId().equals("ebike1") && ebike.newState().get() == Ebike.EbikeState.DISMISSED)); //FIXME:

        // Verify that the ebike was removed from the manager's internal list.
        assertFalse(manager.getAllEbikes().toString().contains("ebike1"), "Ebike1 should no longer exist in the list.");
    }

    @Test
    void testUpdateEbike() throws RepositoryException {
        // Update the state and location of the first ebike.
        manager.updateEbike("ebike1", Optional.of(Ebike.EbikeState.IN_USE), Optional.of(5.0), Optional.of(5.0),
                            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        // Verify that the repository's saveEbike method was called.
        verify(mockRepository, times(1)).saveEbikeEvent(argThat(ebike -> // FIXME
            ebike.ebikeId().equals("ebike1") &&
            ebike.newState().get() == Ebike.EbikeState.IN_USE &&
            ebike.deltaPos().x() == 5.0 &&
            ebike.deltaPos().y() == 5.0));

        // Verify the internal state of the manager reflects the update.
        JsonObject updatedEbike = manager.getEbikeByID("ebike1").orElseThrow();
        assertEquals(Ebike.EbikeState.IN_USE.toString(), updatedEbike.getString("state"), "The state should be updated to IN_USE.");
        assertEquals(5.0, updatedEbike.getDouble("x"), "The X location should be updated to 5.0.");
        assertEquals(5.0, updatedEbike.getDouble("y"), "The Y location should be updated to 5.0.");
    }
}
