function hmm() {
cat <<EOF
Invoke ". scripts/envsetup.sh" from your shell to add the following 
functions to your environment:
EOF
    sort $(gettop)/.hmm |awk -F @ '{printf "%-30s %s\n",$1,$2}'
}

function gettop
{
    local TOPFILE=scripts/envsetup.sh
    if [ -n "$TOP" -a -f "$TOP/$TOPFILE" ] ; then
        echo $TOP
    else
        if [ -f $TOPFILE ] ; then
            # The following circumlocution (repeated below as well) ensures
            # that we record the true directory name and not one that is
            # faked up with symlink names.
            PWD= /bin/pwd
        else
            # We redirect cd to /dev/null in case it's aliased to
            # a command that prints something as a side-effect
            # (like pushd)
            local HERE=$PWD
            T=
            while [ \( ! \( -f $TOPFILE \) \) -a \( $PWD != "/" \) ]; do
                cd .. > /dev/null
                T=`PWD= /bin/pwd`
            done
            cd $HERE > /dev/null
            if [ -f "$T/$TOPFILE" ]; then
                echo $T
            fi
        fi
    fi
}
T=$(gettop)
rm -f $T/.hmm $T/.hmmv
echo "gettop@display the top directory" >> $T/.hmm

function croot()
{
    T=$(gettop)
    if [ "$T" ]; then
        cd $(gettop)
    else
        echo "Couldn't locate the top of the tree.  Try setting TOP."
    fi
}
echo "croot@Change back to the top dir" >> $T/.hmm

function tws
{
  (
  cd $(gettop)/external/tws/IBJts
  java -cp jts.jar:total.2013.jar \
       -Xmx512M \
       -XX:MaxPermSize=128M \
       jclient.LoginFrame .
  )
}
echo 'tws@start tws' >> $T/.hmm

function balance
{
  (
  java -jar $(gettop)/tools/out/artifacts/balance_jar/balance.jar
  )
}
echo 'balance@show the balance of the accounts' >> $T/.hmm

function divden
{
  (
  java -jar $(gettop)/tools/out/artifacts/divden_jar/divden.jar $*
  )
}
echo 'divden@divden' >> $T/.hmm

has-account ()
{
    if [ -n "$IB_ACCOUNT" ]; then
        return 0
    else
        cat <<EOF >&2
You must set up the IB_ACCOUNT variable.

Sorry, but I gotta bail until you get this set up properly.
EOF
        return 1
    fi
}
echo 'has-account@Does the IB_ACCOUNT variable exist?' >> $T/.hmm

unset T f
