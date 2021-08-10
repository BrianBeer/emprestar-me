import requests


class Host :
    def __init__( self) :
        self.base_url = "http://localhost:8080"
        self.session = requests.Session( )

    def path( self, path ):
        return f"{self.base_url}{path}"

    def post( self, url, params=None, json=None, headers=None ) :
        if self.session :
            return self.session.post( self.path(url), data=params, json=json, headers=headers )
        else :
            return requests.post( self.path(url), data=params, json=json, headers=headers )

    def put( self, url, params=None, json=None, headers=None ) :
        if self.session :
            return self.session.put( self.path(url), data=params, json=json, headers=headers )
        else :
            return requests.put( self.path(url), data=params, json=json, headers=headers )

    def patch( self, url, params=None, json=None, headers=None ) :
        if self.session :
            return self.session.patch( self.path(url), data=params, json=json, headers=headers )
        else :
            return requests.patch( self.path(url), data=params, json=json, headers=headers )

    def get( self, url, params = None, headers= None ) :
        if self.session :
            return self.session.get( self.path(url), headers=headers )
        else :
            return requests.get( self.path(url), headers=headers )


