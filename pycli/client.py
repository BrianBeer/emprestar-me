from host import Host 

def get() :
    return Client()

class Client :  

    def __init__( self ) :
        self.host = Host()

    def create_account( self ) :
        return self.host.post( f"/create-account" )

    def create_project( self ) : 
        c = {"name": "brian", "goal":  2332}
        return self.host.post( f"/create-project", json=c )

    def fetch_project( self, id ) : 
        url = f"/project/{id}"
        return self.host.get( url )

    def deposit( self, account_id, amount = 2000 ) : 
        url = f"/account/{account_id}/deposit"
        json = { "amount": amount }
        return self.host.post( url, json = json )

    def invest( self, project_id, account_id, amount = 2000 ) : 
        url = f"/project/{project_id}/invest"
        json = {"accountId": account_id, "amount": amount }
        return self.host.post( url, json = json )
