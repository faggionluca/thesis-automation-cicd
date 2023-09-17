FROM alpine/git

COPY ./git_cred.sh .
RUN chmod +x ./git_cred.sh

LABEL com.lucafagion.thesis="true"

# VOLUME /repo

ENTRYPOINT [ "./git_cred.sh" ]