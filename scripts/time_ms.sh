#!/bin/ksh
if [ ! -x /var/tmp/time_ms ]
then
    cat > /tmp/time_ms.c << %
    #include <sys/time.h>
    main()
    {
        struct timeval tv;
        gettimeofday(&tv,(void*)0);
        printf("%d.%d\n",tv.tv_sec,tv.tv_usec/1000);
    }
%
    PATH=$PATH:/usr/sfw gcc /tmp/time_ms.c -o /var/tmp/time_ms
fi
/var/tmp/time_ms

