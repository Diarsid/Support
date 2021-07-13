module diarsid.support {

    requires java.desktop;
    requires org.slf4j;

    exports diarsid.support.callbacks;
    exports diarsid.support.callbacks.groups;
    exports diarsid.support.concurrency;
    exports diarsid.support.concurrency.async.provider;
    exports diarsid.support.concurrency.stateful.workers;
    exports diarsid.support.concurrency.threads;
    exports diarsid.support.configuration;
    exports diarsid.support.exceptions;
    exports diarsid.support.filesystem;
    exports diarsid.support.functional;
    exports diarsid.support.log;
    exports diarsid.support.lang;
    exports diarsid.support.metainfo;
    exports diarsid.support.misc;
    exports diarsid.support.model;
    exports diarsid.support.network;
    exports diarsid.support.objects;
    exports diarsid.support.objects.collections;
    exports diarsid.support.objects.groups;
    exports diarsid.support.objects.groups.async;
    exports diarsid.support.objects.references;
    exports diarsid.support.objects.workers;
    exports diarsid.support.strings;
    exports diarsid.support.strings.model;
    exports diarsid.support.strings.replace;
    exports diarsid.support.time;
}
