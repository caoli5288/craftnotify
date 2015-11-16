# CraftNotify
Mail notify for minecraft server.

## Mail
- When server crashed.
- when server underling large lag.
- Others...

## Configure
```Yaml
server:
  name: The minecraft server name
  notify:
    crashed: true
    lagged:
      enable: true
      threshold: 15
notify:
  account:
    host: smtp.google.com
    user: root
    pass: anyPassword
    overSSL: true
  sendTo: root@gmail.com
```
