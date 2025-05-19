package star.home.board.service;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.home.board.dto.BoardImageDTO;
import star.common.exception.client.TooManyImageUrlsException;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.BoardImage;
import star.home.board.repository.BoardImageRepository;

@Service
@RequiredArgsConstructor
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;

    @Transactional(readOnly = true)
    public List<BoardImageDTO> getImageUrls(Long boardId) {
        List<BoardImage> boardImageList = boardImageRepository.getBoardImageByBoardIdOrderBySortOrderAsc(
                boardId);

        return boardImageList
                .stream()
                .map(BoardImageDTO::from)
                .toList();
    }

    @Transactional
    public void addImageUrls(Board board, List<String> imageUrls) {
        overwriteImageUrls(board, imageUrls);
    }

    @Transactional
    public void overwriteImageUrls(Board board, List<String> imageUrls) {
        if (imageUrls.size() > MAX_IMAGE_COUNT) {
            throw new TooManyImageUrlsException(MAX_IMAGE_COUNT);
        }

        boardImageRepository.deleteBoardImagesByBoardId(board.getId());

        List<BoardImage> newBoardImages = IntStream.range(0, imageUrls.size())
                .mapToObj(i -> BoardImage.builder()
                        .board(board)
                        .imageUrl(imageUrls.get(i))
                        .sortOrder(i)
                        .build())
                .toList();

        boardImageRepository.saveAll(newBoardImages);
    }

    @Transactional
    public void deleteBoardImageUrls(Long boardId) {
        boardImageRepository.deleteBoardImagesByBoardId(boardId);
    }
}
