#include <iostream>
#include <fstream>
#include <boost/thread.hpp>
#include <boost/date_time.hpp>
#include <stdlib.h>
#include <string>
#include <boost/locale.hpp>
#include "connectionHandler.h"
#include "../encoder/utf8.h"
#include "../encoder/encoder.h"

using namespace std;

bool shouldClose=false;

void talking(ConnectionHandler &connectionHandler) {
	   while (true) {
	        string answer;
	        int len;
	        if (!connectionHandler.getLine(answer)) {
	            cout << "Disconnected. Exiting...\n" << ::endl;
	            break;
	        }

	        len=answer.length();
	        answer.resize(len-1);

	        cout << answer << std::endl << std::endl;
	        if (answer == "SYSMSG QUIT ACCEPTED" || shouldClose) {
	        	shouldClose = true;
	        	connectionHandler.close();
	            std::cout << "Exiting...\n" << std::endl;
	            break;
	        }
	    }

}

void Cin(ConnectionHandler &connectionHandler) {
	while (true) {
		const short bufsize = 1024;
		char buf[bufsize];
		cin.getline(buf, bufsize);
		string line(buf);
		if (!connectionHandler.sendLine(line) || shouldClose) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			shouldClose = true;
			connectionHandler.close();
			break;
		}
	}
}


int main (int argc, char *argv[]) {
	if (argc < 3) {
		std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
		return -1;
	}
	string host = argv[1];
	short port = atoi(argv[2]);

	ConnectionHandler connectionHandler(host, port);
	if (!connectionHandler.connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
		return 1;
	}

	boost::thread stdinListener(Cin, boost::ref(connectionHandler));
	boost::thread serverListener(talking, boost::ref(connectionHandler));

	stdinListener.join();
	serverListener.join();

}
