//
// Created by yen on 5/28/22.
//

#include <jni.h>
#include <cstdio>
#include <cstring>
#include <android/log.h>
#include "socketutil.h"
#include <sys/socket.h>
#include <netinet/in.h>
#include <cstdlib>
#include <android/log.h>
#include <arpa/inet.h>
#include <fstream>
#include <unistd.h>

#define MAXSIZE 4096
struct sockaddr_in sock_add;
struct sockaddr_in server_addr;
int sockfd = 0;
char buff[MAXSIZE];
void release(){
    close(sockfd);
}
int sendData(const char* data, const int len) {
    int offset = 0;
    int sent;

    while (offset < len) {
        sent = send(sockfd, data + offset, len - offset, 0);
        if (sent < 0) return -1;
        offset += sent;
    }
    return 1;
}

int connectRemote(const char* addr, const int port) {
    if (sockfd > 0) return -1;
    sockfd = socket(AF_INET, SOCK_STREAM, 0);//ipv4, TCP data connection
    LOGI("step1");
    if (sockfd <0) {
        LOGI("socket error");
        return -1;
    }
    //server address
    bzero(&server_addr,sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port);
    LOGI("step2");
    if( inet_pton(AF_INET, addr, &server_addr.sin_addr) <0){//Set the ip address
        LOGI("address error");
        return -1;
    }
    socklen_t server_addr_length = sizeof(server_addr);
    LOGI("%s", addr);
    int connfd = connect(sockfd, (struct sockaddr*)&server_addr, server_addr_length);//Connect to the server
    LOGI("step3");
    if (connfd <0) {
        LOGI("connect error");
        return -1;
    }
    return 1;
}