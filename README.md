# DELPHI-COUNCIL - the torganized play configuration

> What man is a man who does not make the world better.
>
> -- Balian, Kingdom of Heaven

![Dependabot](https://flat.badgen.net/dependabot/Paladins-Inn/delphi-council/?icon=dependabot)
![Maven](https://github.com/Paladins-Inn/delphi-council/workflows/CI/badge.svg)
[![Docker Repository on Quay](https://quay.io/repository/klenkes74/delphi-council-is/status "Docker Repository on Quay")](https://quay.io/repository/klenkes74/delphi-council-is)

## License
The license for the software is LGPL 3.0 or newer. Parts of the software may be licensed under other licences like MIT
or Apache 2.0 - the files are marked appropriately.

## Architecture

tl;dr (ok, only the bullshit bingo words):
- Immutable Objects (where frameworks allow)
- Relying heavily on generated code
- 100 % test coverage of human generated code
- Every line of code not written is bug free!

## Distribution
The software is distributed via quay.io. You find the images as

- quay.io/klenkes74/delphi-council-is:0.1.0-SNAPSHOT (bleeding edge)

The images are prepared for consumption by OpenShift 3.11, so they run without any problems on kubernetes, too.

## Installation
You can install the software via helm:

```
export NAMESPACE=<target namespace>
export DB_PASSWORD=$(pwgen 16 1)
export DB_ROOT_PASSWORD=$(pwgen 16 1)
helm upgrade --install delphi-council-is helm/delphi-council-is --namespace ${NAMESPACE} \
             --set-string db.password=${DB_PASSWORD} --set-string db.rootpassword=${DB_ROOT_PASSWORD}
```


## Note from the author
This software is meant do be perfected not finished.

If someone is interested in getting it faster, we may team up. I'm open for that. But be warned: I want to do it
_right_. So no short cuts to get faster. And be prepared for some basic discussions about the architecture or software
design :-).

---
Bensheim, 2021-03-11
