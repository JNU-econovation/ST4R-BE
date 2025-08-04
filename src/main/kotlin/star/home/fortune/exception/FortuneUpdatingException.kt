package star.home.fortune.exception

import star.common.exception.ErrorCode
import star.common.exception.server.InternalServerException

class FortuneUpdatingException : InternalServerException(ErrorCode.FORTUNE_UPDATING), FortuneException