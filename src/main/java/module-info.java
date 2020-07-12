module diarsid.support {

    requires slf4j.api;

    exports diarsid.support.callbacks;
    exports diarsid.support.callbacks.groups;
    exports diarsid.support.concurrency;
    exports diarsid.support.concurrency.async.provider;
    exports diarsid.support.concurrency.stateful.workers;
    exports diarsid.support.concurrency.threads;
    exports diarsid.support.configuration;
    exports diarsid.support.log;
    exports diarsid.support.metainfo;
    exports diarsid.support.objects;
    exports diarsid.support.objects.groups;
    exports diarsid.support.objects.groups.async;
    exports diarsid.support.objects.references;
    exports diarsid.support.objects.references.impl;
    exports diarsid.support.objects.workers;
    exports diarsid.support.strings;
    exports diarsid.support.strings.model;
    exports diarsid.support.strings.replace;
}
