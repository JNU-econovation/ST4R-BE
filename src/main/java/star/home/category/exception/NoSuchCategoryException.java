package star.home.category.exception;

public class NoSuchCategoryException extends RuntimeException {

    public NoSuchCategoryException(String name) {
        super(name + "이라는 카테코리는 존재하지 않습니다.");
    }
}
