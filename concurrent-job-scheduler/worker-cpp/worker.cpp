#include <iostream>
#include <thread>
#include <chrono>

int main(int argc, char* argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: worker <jobName> <executionTimeMs>\n";
        return 1;
    }

    std::string jobName = argv[1];
    int executionTimeMs = std::stoi(argv[2]);

    std::cout << "[C++ WORKER] Started job: " << jobName << std::endl;

    // Simulate CPU / processing work
    std::this_thread::sleep_for(std::chrono::milliseconds(executionTimeMs));

    std::cout << "[C++ WORKER] Completed job: " << jobName << std::endl;

    return 0;
}
