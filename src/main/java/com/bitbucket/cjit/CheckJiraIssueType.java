package com.bitbucket.cjit;

import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.setting.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class CheckJiraIssueType implements RepositoryMergeCheck {
    private static final Logger log = LoggerFactory.getLogger(CheckJiraIssueType.class);

    private final CjitService cjitService;

    public CheckJiraIssueType(CjitService cjitService) {
        this.cjitService = cjitService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(
            @Nonnull PreRepositoryHookContext context,
            @Nonnull PullRequestMergeHookRequest pullRequestMergeHookRequest) {
        final Settings settings = context.getSettings();

        log.debug("cjit settings: {}", settings.asMap());

        return cjitService.check(context, pullRequestMergeHookRequest, settings);
    }
}