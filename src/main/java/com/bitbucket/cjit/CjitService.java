package com.bitbucket.cjit;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.setting.Settings;

public interface CjitService {
    RepositoryHookResult check(PreRepositoryHookContext context,
                               PullRequestMergeHookRequest pullRequestMergeHookRequest, Settings settings);
}