FROM alpine/git

COPY ./git_cred.sh .
RUN chmod +x ./git_cred.sh

VOLUME /repo

ENTRYPOINT [ "./git_cred.sh" ]