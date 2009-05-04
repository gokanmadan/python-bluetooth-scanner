#! /usr/bin/ruby
%{
This is an example of a simple ruby subscriber for a XMPP pubsub setup.

Eventually I will write a tutorial for setting up a nice unsecure ejabberd for pub-sub.

There's a nice tutorial covering part of it at:
http://www.keoko.net/2008/12/xmpp-pubsub-with-ejabberd-and-xmpp4r/

You'll need to:
sudo gem install xmpp4r

}

require "rubygems"
require "xmpp4r"
require "xmpp4r/pubsub"
require "xmpp4r/pubsub/helper/servicehelper.rb"

require "xmpp4r/pubsub/helper/nodebrowser.rb"
require "xmpp4r/pubsub/helper/nodehelper.rb" include Jabber
#Jabber::debug = true

jid = 'sub@pubjab.be-n.com/laptop'
password = 'rocket'
node = '/home/pubjab.be-n.com/ben/btscan'
service = 'pubsub.pubjab.be-n.com'

# connect XMPP client
client = Client.new(JID.new(jid))
# remove "127.0.0.1" if you are not using a local ejabberd
client.connect("127.0.0.1")
client.auth(password)
client.send(Jabber::Presence.new.set_type(:available))
sleep(1)
# subscribe to the node
pubsub = PubSub::ServiceHelper.new(client, service)
pubsub.subscribe_to(node)
subscriptions = pubsub.get_subscriptions_from_all_nodes()
puts "subscriptions: #{subscriptions}\n\n"
puts "events:\n"

# set callback for new events

pubsub.add_event_callback do |event|
begin
event.payload.each do |e|
puts e,"----\n"
end
rescue
puts "Error : #{$!} \n #{event}"

end
# infinite loop
loop do
sleep 1
end
