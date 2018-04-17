package com.bitbucket.cjit;

import com.atlassian.bitbucket.hook.repository.*;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.integration.jira.JiraIssueService;
import com.atlassian.bitbucket.integration.jira.JiraIssue;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CjitServiceImpl implements CjitService {
    private static final Logger log = LoggerFactory.getLogger(CjitServiceImpl.class);
    private final JiraIssueService jiraIssueService;
    public CjitServiceImpl(JiraIssueService jiraIssueService) {this.jiraIssueService = jiraIssueService;}
    @Override
    public RepositoryHookResult check(PreRepositoryHookContext context,
                                      PullRequestMergeHookRequest pullRequestMergeHookRequest, Settings settings) {
        log.debug("CjitHook preUpdate, registering commit callback. settings={}", settings);

        PullRequest pullReq = pullRequestMergeHookRequest.getPullRequest();
        Repository targetRepo = pullReq.getToRef().getRepository();
        int repoId = targetRepo.getId();
        Long pullReqId = pullReq.getId();
        log.debug("NewrepoId = {}", repoId);
        log.debug("pullReqId is: {}", pullReqId);

        PullRequest refchanges = pullRequestMergeHookRequest.getPullRequest();
        log.debug("REF CHANGES in PULL_REQ. 1. FROM REF = {} 2. TO REF = {}", refchanges.getFromRef(), refchanges.getToRef());
        String pullReqLastCommit = pullRequestMergeHookRequest.getPullRequest().getFromRef().getLatestCommit();
        String pullreqdesc = pullReq.getDescription();
        log.debug("PULL REQ DESCRIPTION: {}", pullreqdesc);

        /* GIT Command to list commits between last commit in target branch and commits made in PR feature branch
        git.exe rev-list --format=%H%x02%P%x02%aN%x02%aE%x02%at%x02%cN%x02%cE%x02%ct%n%B%n%x03END%x04 --topo-order b1bbb00f2f43e839662a87db4ddd492b78057a98 ^848d0bbdd2aa223ed0cd4490c44dc7ec698309c1 --
         */
        Set<JiraIssue> issueset = jiraIssueService.getIssuesForPullRequest(repoId, pullReqId);
        for (JiraIssue issue : issueset) {
            log.debug("FOUND ISSUES FROM PULL REQ: {}", issue.getKey());
        }

        if (issueset.isEmpty()) {
            return RepositoryHookResult.rejected("PR rejected", "No valid JIRA issues found in pullrequest. Please add a valid JIRA issue to commits, title or description of pullrequest \n");
        }
        return RepositoryHookResult.accepted();
    }
}
