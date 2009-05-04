
#---------------------------------------------------------
#  Copyright (c) 2002-2004 Nokia. All rights reserved. 
#  This material, including documentation and any related 
#  computer programs, is protected by copyright controlled by 
#  Nokia. All rights are reserved. Copying, including 
#  reproducing, storing, adapting or translating, any 
#  or all of this material requires the prior written consent of 
#  Nokia. This material also contains confidential 
#  information which may not be disclosed to others without the 
#  prior written consent of Nokia. 
#---------------------------------------------------------

"""
eb2lib - Python library for communicating with the Nokoscope server

$Date: 2008-07-23 00:35:22 -0700 (Wed, 23 Jul 2008) $ 
$Rev: 2214 $  
$Author: rihankin $ 
"""

__version__ = "0.10"
__author__ = "Context Content Community @ Palo Alto SRC (C3)"

import httplib, urllib
import time
import simplejson

# for using compression
import gzip
import cStringIO

SERVER = "alpha.nokoscope.com"
PATH = "/eb2/"
VERSION = "2.00"
SOURCE = "eb2lib.py" # XXX version, from SVN

class NokoscopeError(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

class Nokoscope:
    """ The Nokoscope API """

    def __init__(self, username, password=None, token=None, 
            server=SERVER, path=PATH):
        """ 
        Create an Nokoscope object. 
        Password or token must be present
        """
        self.login = username      
        if password is None and token is None:
            raise NokoscopeError, u"password or token must be given"

        if token is None:
            token = create_token(password)

        self.password = token
        self.version = VERSION 
        self.server = server
        if not path:
            self.path = "/"
        else:
            self.path = path

    def get(self, type='', filter_empty='', limit=10, offset=0, 
            start_time='', end_time='', compress=False, ssl=True):
        """ 
        Retrieve data from the Nokoscope server 
        Optionally, set ssl to False to turn off secure sockets
        """
        args = []
        args.append(self._user_id())
        args.append('type=' + type)
        args.append('filter_empty=' + filter_empty)
        args.append('limit=' + str(limit))
        args.append('offset=' + str(offset))
        if start_time:
            args.append('from_time=' + str(int(start_time)))
        if end_time:
            args.append('to_time=' + str(int(end_time)))
        if compress:
            args.append('compress=' + 'true')

        url = self.path + 'get?' + '&'.join(args)

        if ssl:
            h = httplib.HTTPSConnection(self.server)
        else:
            h = httplib.HTTPConnection(self.server)

        a = time.clock()

        h.request('GET', url)
        resp = h.getresponse()
        r = {'status':resp.status, 'reason':resp.reason, 'data':resp.read()}
        h.close()

        b = time.clock()
 
        self.stats = {'msgsz':len(r['data']), 'time':b-a}

        if compress:
            zbuf = cStringIO.StringIO(r['data'])
            zfile = gzip.GzipFile(fileobj = zbuf)
            r['data'] = zfile.read()
            zfile.close()

        if r['status'] == 200:
            return json_decode(r['data'])
        else:
            raise NokoscopeError, r['reason']

    def put(self, recs, ssl=True):
        """ 
        Insert data into the Nokoscope server 
        Optionally, set ssl to False to turn off secure sockets
        """
        payload = json_encode(recs)
        body = urllib.urlencode({"image_file":payload})
        url = self.path + 'put?' + self._user_id()

        a = time.clock()

        headers = {"Content-type": "application/x-www-form-urlencoded"}

        if ssl:
            h = httplib.HTTPSConnection(self.server)
        else:
            h = httplib.HTTPConnection(self.server)

        h.request('POST', url, body, headers)
        resp = h.getresponse()
        r = {'status':resp.status, 'reason':resp.reason, 'data':resp.read()}
        h.close()

        b = time.clock()

        self.stats = {'msgsz':len(payload), 'time':b-a}

        if r['status'] == 200:
            return json_decode(r['data'])
        else: 
            raise NokoscopeError, r['reason']

    def _user_id(self):
        return "ver=%s&user=%s&token=%s" % (self.version, self.login, self.password)

def iso_to_unix(t):
    """ Convert iso time to unix time """
    a = "%Y-%m-%dT%H:%M:%S"
    b = time.strptime(t,a)
    return time.mktime(b)

def json_encode(o):
    return simplejson.dumps(o)

def json_decode(s):
    return simplejson.loads(s)

def create_token(p):
    """ Create an Nokoscope token from a password """
    import sha
    return sha.sha(p).hexdigest()

def create_rec(type, data, source=SOURCE, timestamp=time.time(), 
        tz=time.timezone, ver='0.10'):
    """ Helper to create a valid Nokoscope record """

    d = {}
    d['type'] = type
    d['data'] = data
    d['ver'] = ver
    d['source'] = source
    d['time'] = timestamp
    d['tz'] = tz
    return d

def join_by_time(window, recs):
    ''' Perform time-based join of a record stream with itself. 
        The acceptable window size must be provided by the user.
        *Records are expected in sorted order.*
        Returns: 2-tuple of joined records
    '''
    res = []
    for idx in range(len(recs)):
        i = recs[idx]
        for j in recs[idx+1:]:
            if abs(i['time'] - j['time']) <= window:
                res.append( (i,j) )
    return res

def Nokoscope_prompt():
    """ 
    Interactive creation of Nokoscope object.
    Prompts the user for the username and password to create a 
    valid Nokoscope record 
    """
    import getpass
    print 'login to %s' % SERVER
    user = raw_input("username: ")
    p = getpass.getpass("password: ")
    return Nokoscope(user, password=p)

def test():
    """ Test the functionality by getting and putting data into Nokoscope""" 
    eb = Nokoscope_prompt()
    print eb.get(type='gps', filter_empty='gps', limit=1)
    print eb.stats
    a = create_rec(type='tags', data=['ready to go home'])
    print a
    # print eb.put([a])

if __name__ == "__main__":
    test()

