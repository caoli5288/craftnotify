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
    lagged:
      threshold: 15
notify:
  from: mengcraft@qq.com
  smtp:
    host: smtp.qq.com
    port: 25
    user: mengcraft
    pass: anyPassword
    sign: false
```
