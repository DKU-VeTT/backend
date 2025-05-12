package kr.ac.dankook.VettPlaceServer.repository;

import kr.ac.dankook.VettPlaceServer.document.Place;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends MongoRepository<Place, String> {
    List<Place> findByCategory(String category);

    @Aggregation("{ '$group': { '_id': '$category' } }")
    List<String> findDistinctCategories();
    List<Place> findByPlaceNameContainingIgnoreCase(String keyword);
    List<Place> findByCategoryAndPlaceNameContaining(String category,String placeName);
}
