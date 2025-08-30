package emad.space.domain.handleState

class ConnectionError(message: String = "No internet connection please check your internet") :
    Throwable(message) {}