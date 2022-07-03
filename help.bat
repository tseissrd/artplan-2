@echo off
echo SAMPLES USAGE:
echo register {username} {password}
echo - register a new user
echo.
echo login {username} {password}
echo - log into system (get token)
echo.
echo checkname {username}
echo - check name availability
echo.
echo createAnimal {name} {username:password\username:token}
echo - create sample animal with specified name (outputs id of the created animal)
echo.
echo getAnimal {id} {username:password\username:token}
echo - get details about animal with specified name
echo.
echo getAnimalList {username:password\username:token}
echo - get list of animals created by current user
echo.
echo updateAnimal {id} {name} {username:password\username:token}
echo - update name of the animal with the specified id
echo.
echo deleteAnimal {id} {username:password\username:token}
echo - delete animal entry
echo.
echo help
echo - show this text